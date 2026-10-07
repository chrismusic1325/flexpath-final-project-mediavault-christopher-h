FROM us-central1-docker.pkg.dev/hire-human/hire-human-ai/agentic_engineer_1:latest

# MediaVault project-specific tooling.
# The course image already provides the coding-agent harness, Python,
# Node.js/npm, git, curl, bash, and other baseline utilities.
RUN apt-get update && apt-get install -y --no-install-recommends \
    openjdk-17-jdk-headless \
    maven \
    default-mysql-client \
    && rm -rf /var/lib/apt/lists/*

# Pre-fetch Maven dependencies so repeated disposable containers begin with
# a more reproducible backend dependency cache.
COPY backend/pom.xml /tmp/mediavault-backend/pom.xml
RUN cd /tmp/mediavault-backend && mvn -B dependency:go-offline

# Keep the agent's working directory aligned with the course convention.
WORKDIR /workspace

CMD ["/bin/bash"]
