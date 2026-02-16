package com.petcare.service.impl;

import com.petcare.dto.UserAddressDTO;
import com.petcare.entity.UserAddress;
import com.petcare.repository.UserAddressRepository;
import com.petcare.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Override
    public UserAddressDTO getDefaultAddressForUser(Long userId) {
        Optional<UserAddress> opt = userAddressRepository.findFirstByUserIdAndIsDefaultTrue(userId);
        if (opt.isEmpty()) return null;
        return toDto(opt.get());
    }

    @Override
    public UserAddressDTO saveOrUpdateDefaultAddress(Long userId, UserAddressDTO dto) {
        // clear existing defaults
        userAddressRepository.findByUserId(userId).forEach(a -> {
            if (Boolean.TRUE.equals(a.getIsDefault())) {
                a.setIsDefault(false);
                userAddressRepository.save(a);
            }
        });

        UserAddress entity = new UserAddress();
        entity.setUserId(userId);
        entity.setFullName(dto.getFullName());
        entity.setLine1(dto.getLine1());
        entity.setLine2(dto.getLine2());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(dto.getCountry());
        entity.setPhone(dto.getPhone());
        entity.setIsDefault(Boolean.TRUE.equals(dto.getIsDefault()) || dto.getIsDefault() == null);

        UserAddress saved = userAddressRepository.save(entity);
        return toDto(saved);
    }

    private UserAddressDTO toDto(UserAddress a) {
        UserAddressDTO d = new UserAddressDTO();
        d.setId(a.getId());
        d.setFullName(a.getFullName());
        d.setLine1(a.getLine1());
        d.setLine2(a.getLine2());
        d.setCity(a.getCity());
        d.setState(a.getState());
        d.setPostalCode(a.getPostalCode());
        d.setCountry(a.getCountry());
        d.setPhone(a.getPhone());
        d.setIsDefault(a.getIsDefault());
        return d;
    }
}
