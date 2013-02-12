# keytool reference at http://docs.oracle.com/javase/1.4.2/docs/tooldocs/windows/keytool.html and elsewhere
keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048 -dname "cn=replace-with-server-name, ou=org-name, o=org-name, c=IL" -keypass password
