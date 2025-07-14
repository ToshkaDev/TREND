FROM openjdk:8-jdk-slim-buster

# Install dependencies and Python 2
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        python2.7-dev \
        libblas-dev liblapack-dev libatlas-base-dev gfortran libdw1 \
        python2.7 \
        python-qt4 xvfb xauth \
        perl \
        curl \
        bash && \
    ln -s /usr/bin/python2.7 /usr/bin/python && \
    curl -sS https://bootstrap.pypa.io/pip/2.7/get-pip.py | python && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# python-qt4 xvfb are needed to render images using ete2
# libdw1 is needed for rpsbproc
# python2.7-dev is needed to install biopython==1.76 as it contains C extensions
WORKDIR /app

# Copy built Spring Boot JAR
COPY target/*.jar app.jar

# Copy Python scripts and install dependencies
COPY bioinformatics-programs/ /app/bioinformatics-programs/
RUN pip2 install --no-cache-dir -r /app/bioinformatics-programs/requirements.txt

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]