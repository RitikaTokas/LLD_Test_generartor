FROM openjdk:17-slim

# Install Java (before switching user)
RUN apt update && apt install -y openjdk-17-jdk

# Create non-root user
RUN useradd -m -u 1001 sandboxuser
WORKDIR /app/
# Copy and fix permissions for entrypoint script
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
RUN chown -R sandboxuser:sandboxuser /app/

# Switch to non-root user
USER sandboxuser

# Set entrypoint
ENTRYPOINT ["/app/entrypoint.sh"]
