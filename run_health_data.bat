@echo off
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p12345 petcare < reset_and_add_health_data.sql
echo.
echo Health records added successfully!
pause
