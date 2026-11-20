# Security patch release built on top of the published v1.5.0 image.
#
# trend:v1.5.1 = bioliners/trend:v1.5.0 plus:
#  - the Spring Boot jar rebuilt from this repo, with RequestValidator (command
#    injection fix) and the PostgreSQL JDBC 42.7.4 driver (CVE-2024-1597)
#  - the three Python scripts changed by the command injection fix
# All bioinformatics tools and databases come unchanged from v1.5.0, so the image
# builds in minutes, and hosts that already have v1.5.0 only download ~50 MB.
#
# Build from a clean export of a commit (avoids CRLF line endings from Windows
# checkouts ending up in the jar), then publish:
#   git -c core.autocrlf=false archive --format=tar HEAD | \
#     docker build -f docker/backend.patch.Dockerfile -t <namespace>/trend:v1.5.1 -
#   docker push <namespace>/trend:v1.5.1
# (currently published as biswasaneel/trend:v1.5.1)
# For a full from-scratch image, use docker/backend.Dockerfile instead.

# Stage 1: build the jar (same toolchain as docker/backend.Dockerfile) and
# prepare the patched scripts
FROM maven:3.6.3-openjdk-8-slim AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -q clean package -DskipTests && cp target/TREND-*.jar /build/app.jar
COPY bioinformatics-programs/alignAndCunstructTree.py \
     bioinformatics-programs/getDomainsFromHMMScanOrRpsAndTm.py \
     bioinformatics-programs/runCdHit.py \
     /build/scripts/
RUN sed -i 's/\r$//' /build/scripts/*.py && chmod 644 /build/app.jar /build/scripts/*.py

# Stage 2: patch layer on top of v1.5.0
FROM bioliners/trend:v1.5.0
COPY --from=builder /build/app.jar /app/app.jar
COPY --from=builder /build/scripts/ /app/bioinformatics-programs/
