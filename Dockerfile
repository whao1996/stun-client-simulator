FROM java8:8u432

WORKDIR /change

COPY target/app.jar /change

CMD java $JAVA_OPTS -jar /change/app.jar