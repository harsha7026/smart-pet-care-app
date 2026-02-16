@echo off
echo Updating product images in PetCare database...
echo.

mysql -u root -p petcare < update_product_images.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================================
    echo SUCCESS! Product images have been updated.
    echo ================================================
) else (
    echo.
    echo ================================================
    echo ERROR: Failed to update product images.
    echo Please check your MySQL credentials.
    echo ================================================
)

echo.
pause
