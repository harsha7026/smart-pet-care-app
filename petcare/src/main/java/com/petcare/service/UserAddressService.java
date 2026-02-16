package com.petcare.service;

import com.petcare.dto.UserAddressDTO;

public interface UserAddressService {
    UserAddressDTO getDefaultAddressForUser(Long userId);
    UserAddressDTO saveOrUpdateDefaultAddress(Long userId, UserAddressDTO dto);
}
