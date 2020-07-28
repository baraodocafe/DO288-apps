# Use the httpd-parent image as base
FROM quay.io/redhattraining/httpd-parent

EXPOSE 8080
#8.3. Include the io.openshift.expose-service label to indicate the changed port
#that the web server runs on:
LABEL io.openshift.expose-services="8080:http"

#Update the list of labels to include the io.k8s.description, io.k8s.display-
#name, and io.openshift.tags labels that OpenShift consumes to provide helpful
#metadata about the container image:
LABEL io.k8s.description="A basic Apache HTTP Server child image, uses ONBUILD" \
io.k8s.display-name="Apache HTTP Server" \
io.openshift.expose-services="8080:http" \
io.openshift.tags="apache, httpd"

#8.4. You need to run the web server on an unprivileged port (that is, greater than 1024). Use
#a RUN instruction to change the port number in the Apache HTTP Server configuration
#file from the default port 80 to 8080:
RUN sed -i "s/Listen 80/Listen 8080/g" /etc/httpd/conf/httpd.conf

#8.5. Change the group ID and permissions of the folders where the web server process
#reads and writes files:
RUN chgrp -R 0 /var/log/httpd /var/run/httpd && \
chmod -R g=u /var/log/httpd /var/run/httpd

#8.6. Add a USER instruction for an unprivileged user. The Red Hat convention is to use
#userid 1001:
USER 1001
