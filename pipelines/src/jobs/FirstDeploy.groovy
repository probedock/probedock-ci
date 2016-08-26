def executeJob() {
    load('ci/pipelines/src/utils/LoadEnv.groovy').setupEnv(env, '/envs/' + env.PROBEDOCK_ENV)

    def Launcher = load 'ci/pipelines/src/utils/Launcher.groovy'
    def Passwords = load 'ci/pipelines/src/utils/Passwords.groovy'

    /**
     * Start the Nginx
     */
    stage 'Start the reverse proxy (Nginx)'
    sh 'ci/pipelines/scripts/nginx.sh'

    /**
     * Start the PostgreSQL database server
     */
    stage 'Start PostgresSQL'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.POSTGRESSQL_PASSWORD_NAME, variable: Passwords.DOCKER_POSTGRESQL_PASSWORD_VARNAME],
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'ci/pipelines/scripts/postgres.sh'
    }

    /**
     * Build the Probe Dock main image
     */
    stage 'Build Probe Dock docker image'
    sh 'ci/pipelines/scripts/probedock-docker-images.sh'

    /**
     * Create the database
     */
    stage 'Setup the database'
    withCredentials([
        [$class: 'StringBinding', credentialsId: Passwords.PROBEDOCK_DB_PASSWORD_NAME, variable: Passwords.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME]
    ]) {
        sh 'ci/pipelines/scripts/setup-database.sh'
    }

    /**
     * We want to create the admin user
     */
    stage 'Create the admin user'
    Launcher.launchJob(Launcher.JOB_CREATE_ADMIN, false)

    stage 'Deploy Probe Dock'
    Launcher.launchJob(Launcher.JOB_DEPLOY, false)

    stage 'First deployment finalization'
    sh 'touch /envs/.' + env.PROBEDOCK_ENV + '-firstDeploymentDone'
}

return this