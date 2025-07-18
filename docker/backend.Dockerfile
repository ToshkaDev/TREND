# Stage 1: Build JAR
FROM maven:3.6.3-openjdk-8-slim AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests


# Stage 2: Final production image with Python and the JAR
FROM openjdk:8-jdk-slim-buster

# Fix old Debian Buster repo URLs
RUN sed -i 's|http://deb.debian.org/debian|http://archive.debian.org/debian|g' /etc/apt/sources.list && \
    sed -i 's|http://security.debian.org/|http://archive.debian.org/|g' /etc/apt/sources.list && \
    echo 'Acquire::Check-Valid-Until "false";' > /etc/apt/apt.conf.d/99no-check-valid-until

# Install dependencies and Python 2
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        python2.7-dev \
        libblas-dev liblapack-dev libatlas-base-dev gfortran libdw1 zlib1g-dev \
        python2.7 \
        python-qt4 xvfb xauth \
        perl \
        curl \
        bash && \
    ln -sf /usr/bin/python2.7 /usr/bin/python && \
    curl -sS https://bootstrap.pypa.io/pip/2.7/get-pip.py | python && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# python-qt4 xvfb are needed to render images using ete2
# libdw1 is needed for rpsbproc
# zlib1g-dev is needed to compile cd-hit
# python2.7-dev is needed to install biopython==1.76 as it contains C extensions
WORKDIR /app

# Copy built Spring Boot JAR
COPY --from=builder /build/target/*.jar app.jar

# Install all the necessary programs
COPY util/ /app/util/
RUN chmod +x /app/util/*
RUN bash /app/util/install-software.sh

# Copy Python scripts and install dependencies
COPY bioinformatics-programs/ /app/bioinformatics-programs/
RUN pip2 install --no-cache-dir -r /app/bioinformatics-programs/requirements.txt

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]