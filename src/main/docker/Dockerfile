FROM openjdk:11-jre-slim

RUN ulimit -t 5
RUN ulimit -v 262144

RUN useradd -m sandboxuser

WORKDIR /app

COPY entrypoint.sh /app/entrypoint.sh
RUN chmod 755 /app/entrypoint.sh

USER sandboxuser  # Move this AFTER setting permissions

ENTRYPOINT ["/app/entrypoint.sh"]
