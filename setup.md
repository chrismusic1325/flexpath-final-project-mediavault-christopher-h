# MediaVault Agent Sandbox Setup

## Target Codebase

MediaVault is a full-stack media collection and curation application using:

- Java 17
- Spring Boot 3
- Maven
- Spring JDBC
- MySQL
- React 18
- Vite
- Jest
- ESLint

Repository:

`https://github.com/chrismusic1325/flexpath-final-project-mediavault-christopher-h`

## Project Services and Environment Variables

The backend expects a MySQL database. The application references:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

The defaults point to a local MySQL database named `flexpath_final`.

Real credentials are not baked into the Docker image. For sandbox work, test values,
a disposable test database, or omitted credentials should be used whenever the task
does not require a live database.

## Filesystem Boundary

The smallest useful host mount is the root MediaVault repository itself.

Example:

```powershell
-v "${PWD}:/workspace"
```

The sandbox must not mount the Windows home directory, Desktop, Downloads, SSH
directory, browser data, cloud credentials, or unrelated repositories.

## Build Command

From the MediaVault repository root:

```powershell
docker build -t mediavault-agent .
```

## Authentication Volume

If Claude Code is used with the course image, create the course authentication
volume once:

```powershell
docker volume create claude-auth
```

The volume is only for the course image's Claude authentication handling. Project
deliverables still belong in `/workspace`.

## Normal Agent Run

For a network-enabled agent session:

```powershell
docker run -it --rm `
  -v "${PWD}:/workspace" `
  -v "claude-auth:/claude-auth" `
  mediavault-agent
```

Inside the container, launch the supported coding agent, for example:

```bash
claude
```

or:

```bash
opencode
```

## No-Network Boundary Test

For tasks that need only local files:

```powershell
docker run -it --rm `
  --network none `
  -v "${PWD}:/workspace" `
  mediavault-agent
```

Inside the container:

```bash
curl https://example.com
```

Expected result: the request fails because outbound network access is disabled.

## Existing Validation Commands

Backend:

```bash
cd /workspace/backend
mvn test
mvn verify
```

A successful `mvn test` means the configured backend automated tests passed.
`mvn verify` additionally runs the configured JaCoCo verification phase, which
includes a 70% minimum line-coverage rule.

Frontend:

```bash
cd /workspace/frontend
npm ci
npm test
npm run build
npm run lint
```

The frontend also defines:

```bash
npm run verify
```

which runs the build, Jest tests, and ESLint together.

## Smoke-Test Prompt

A low-risk smoke-test prompt for the coding agent is:

> Inspect the MediaVault repository under /workspace. Summarize the backend,
> frontend, database, and test structure. Write the summary to
> /workspace/agent-summary.md. Do not modify any other project files and do not
> access paths outside /workspace.

After the agent finishes:

```bash
cat /workspace/agent-summary.md
```

Exit the container and confirm `agent-summary.md` exists in the MediaVault
repository on the host.

## Smoke-Test Status / Terminal Evidence

The prescribed local Docker Desktop path was attempted on the Windows ARM laptop,
but Docker Desktop could not create its WSL2 Linux VM. The observed host-level
error was:

```text
Wsl/Service/RegisterDistro/CreateVm/HCS/E_NOTIMPL
Nested virtualization is not supported on this machine.
```

Because the Docker engine could not start on this host, the MediaVault coding-agent
smoke test itself was not completed locally. This is a host-runtime limitation, not
a passing sandbox test. The commands above are the baseline invocation to rerun on
a supported Docker host or course-provided environment.

## Security Decisions

### Why did I mount only this folder?

I mounted only the MediaVault repository because it is the smallest host directory
that contains everything the coding agent needs to inspect and modify the project.
This prevents the agent from inheriting access to unrelated repositories, SSH keys,
browser data, downloads, credentials, and other files in my Windows user profile.

### What did I choose to keep ephemeral?

Temporary files, scratch work, temporary downloads, logs that do not need to become
project evidence, and other container-local data remain ephemeral. These belong in
locations such as `/tmp` and disappear with the disposable `--rm` container.

### What did I choose to persist?

Source-code changes, tests, documentation, and other intended project deliverables
persist through the `/workspace` bind mount because it is backed by the MediaVault
repository on the host. If Claude Code is used, its course authentication file may
also persist separately through the Docker-managed `claude-auth` volume.

### What dependencies did I include in the extended Docker image?

The extended image adds OpenJDK 17, Maven, and the MySQL command-line client for
MediaVault's Java/Spring Boot/MySQL backend. The course base image already supplies
the agent harness plus Node.js/npm, Python, Git, curl, bash, Claude Code, and
OpenCode. Maven backend dependencies are pre-fetched during image build with
`mvn dependency:go-offline`.

### What did the smoke test prove?

The planned smoke test is designed to prove that the agent can inspect the
MediaVault repository through `/workspace`, create one controlled documentation
file inside that mounted directory, and leave that file available after the
container exits. On this specific Windows ARM host, the smoke test did not reach
the agent stage because the local WSL2 Docker VM failed to start with `E_NOTIMPL`.
Therefore the local failure proved a host-runtime limitation, not successful
sandbox containment.

### What risks remain?

A mounted `/workspace` is intentionally writable, so an agent could still damage
project files inside the repository. Network-enabled agent sessions can also reach
external services unless egress is restricted. A named authentication volume is
not encryption and must not be shared. Tests may require a database, and real
database credentials should never be baked into the image. Agent-generated changes
still require human review, tests, Git history, and later CI checks before they
should be accepted.
