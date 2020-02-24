mkdir src\main\java\org\metacow\InPowerWeEntrustLoveAdvocated
copy src\*.* src\main\java\org\metacow\InPowerWeEntrustLoveAdvocated
mvn package
copy InPowerWeEntrustLoveAdvocated\target\InPowerWeEntrustLoveAdvocated-1.0-SNAPSHOT-jar-with-dependencies.jar c:\prod
rem copy InPowerWeEntrustLoveAdvocated\config.json c:\prod
RMDIR /S /Q src\main