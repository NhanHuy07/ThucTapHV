#!/bin/bash
# Chạy nhanh nhất - skip tests và compile tối thiểu
mvn spring-boot:run -DskipTests -Dmaven.test.skip=true