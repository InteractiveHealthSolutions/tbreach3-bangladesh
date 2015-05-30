=== TB REACH 3 ===
Contributors: IRD
Software Type: Free, Open-source
Requires: Microsoft Windows 7 or higher, Oracle Java Runtime Environment (JRE) v6.0 or higher, Eclipse Helios or higher
License: GPLv3

== Installation ==
1. Install Oracle Java v6.0.x or higher 
2. Install tomcat v6.0.x
3. Install MySQL Server v5.0 or higher
4. Install OpenMRS 1.9.x with compatible HTML Forms entry module


--------- Server ---------
1. Installing pre-requisites
a. apt-get install mysql
b. apt-get install tomcat6

2. Creating SSL certificate
a. keytool -genkey -keyalg RSA -alias ihscertificate -keystore ihscertificate.ks -validity 720 -keysize 1024

3. Creating PEM format file (standard format for openSSL)
* This file is used by the client (android) to shake hand with the server on secure socket

-- After creating certificate and configuring tomcat --
1. Open website and view certificate
2. Export certificate as pem file
3. Install Keystore Explorer
4. Create new keystore and import certificate exported previously
5. Save keystore as .ks file, this will be loaded as a raw resource in your Android client
-- End certificate thing

4. Enabling SSL encryption on Tomcat
a. nano /var/lib/tomcat6/conf/server.xml
b. Search and uncomment the commented block for configuring SSL HTTP connector (by default, it's on port 8443)
 <Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
               maxThreads="150" scheme="https" secure="true"
               clientAuth="false" sslProtocol="TLS" />
c. Set protocol="org.apache.coyote.http11.Http11NioProtocol", add keystoreFile="/home/myhome/IHSCertificate.cert" and password (if provided while creating the certificate)
    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol" SSLEnabled="true"
               maxThreads="150" scheme="https" secure="true"
               clientAuth="false" sslProtocol="TLS" 
               keystoreFile="/home/myhome/ihscertificate.cer" 
               keystorePass="mysslcertificatepassword" />
d. Save the file and exit the editor
e. service tomcat6 restart
f. In the browser, goto "https://localhost:8443"
g. If the browser warns about untrusted source. Ignore and proceed by adding the website as an exception.

5. Install OpenMRS
a. Goto "https://localhost:8443/manager/html"
b. Deploy the provided "openmrs.war" file and launch
c. Do the initial setup and start OpenMRS

6. Add openmrs database restore procedure
...

7. Restricting OpenMRS to HTTPS only
a. nano /var/lib/tomcat6/webapps/openmrs/WEB-INF/web.xml
b. Add the following text at the end, just before ending tag:
<security-constraint>
	<web-resource-collection>
		<web-resource-name>HTTPSOnly</web-resource-name>
		<url-pattern>/*</url-pattern>
	</web-resource-collection>
	<user-data-constraint>
		<transport-guarantee>CONFIDENTIAL</transport-guarantee>
	</user-data-constraint>
</security-constraint>
c. service tomcat6 restart

7. Installing required modules
- HTML Form Entry Module:
- Reporting Module:
- Reporting Compatability Module:
- Usage Statistics Module:
- Address Hierarchy Module:
- Metadata Sharing Module:
- REST Module:

8. Troubleshooting memory leak issue
- nano /var/lib/tomcat6/conf/web.xml file
- In the jsp servlet definition add the following element:
<init-param>
	<param-name>enablePooling</param-name>
	<param-value>false</param-value>
</init-param>
- If the above doesn't work out, try the following:
- nano /etc/init.d/tomcat6
- Change (~line 81):

FROM:
if [ -z "$JAVA_OPTS" ]; then
        JAVA_OPTS="-Djava.awt.headless=true -Xmx128M"
fi

TO:

if [ -z "$JAVA_OPTS" ]; then
        JAVA_OPTS="-Djava.awt.headless=true -Xmx1024M -Xms1024M -XX:PermSize=256m -XX:MaxPermSize=256m -XX:NewSize=128m"
fi

--------- Client ---------
2. Installing required applications
a. Install Barcode application from Google Playstore
b. Install "tbreach3mobile.apk" copied to the memory
c. Copy tutorial videos to the memory

3. Disable unwanted applications
...

4. Setting up application preferences
...

References:
1. http://owaisahussain.blogspot.com/2013/02/4-step-configuration-of-ssl-encryption.html
