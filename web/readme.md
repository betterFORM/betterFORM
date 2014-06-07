# Important

Special configuration is necessary to ensure correct character encoding on several servers like Tomcat (Jetty probably too).

By setting following system properties when running the server it will be made sure that UTF-8 is used everywhere:

`-Djavax.servlet.request.encoding=UTF-8 -Dfile.encoding=UTF-8`

