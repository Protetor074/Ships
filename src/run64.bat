for /l %%i in (1,1,64) do (
            start cmd.exe @cmd /k "java BuoyMain "%%i""
        )