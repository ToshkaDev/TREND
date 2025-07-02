FROM openjdk:8-jdk-slim-buster

# Install dependencies and Python 2
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        python2.7-dev \
        libblas-dev liblapack-dev libatlas-base-dev gfortran \
        python2.7 \
        curl \
        bash && \
    ln -s /usr/bin/python2.7 /usr/bin/python && \
    curl -sS https://bootstrap.pypa.io/pip/2.7/get-pip.py | python && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Java project files
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src

# Copy Python scripts and install dependencies
COPY bioinformatics-programs/ /app/bioinformatics-programs/
RUN pip2 install --no-cache-dir -r /app/bioinformatics-programs/requirements.txt

# Enable execution of Maven wrapper
RUN chmod +x mvnw

EXPOSE 8080

# Hot reload via spring-boot:run
CMD ["./mvnw", "spring-boot:run"]