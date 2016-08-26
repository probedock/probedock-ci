/**
 * Define the password base names
 */
this.POSTGRESSQL_PASSWORD_BASE_NAME = 'PostgreSQLRoot'
this.PROBEDOCK_DB_PASSWORD_BASE_NAME = 'ProbeDockPostgreSQL'
this.PROBEDOCK_SECRET_KEY_BASE_NAME = 'SecretKeyBase'
this.PROBEDOCK_JWT_SECRET_BASE_NAME = 'JWTSecret'
this.PROBEDOCK_SMTP_USER_BASE_NAME = 'ProbeDockSmtpUser'
this.PROBEDOCK_SMTP_PASSWORD_BASE_NAME = 'ProbeDockSmtpPassword'

/**
 * Define the password names
 */
this.POSTGRESSQL_PASSWORD_NAME = env.PROBEDOCK_ENV + '-' + this.POSTGRESSQL_PASSWORD_BASE_NAME
this.PROBEDOCK_DB_PASSWORD_NAME = env.PROBEDOCK_ENV + '-' + this.PROBEDOCK_DB_PASSWORD_BASE_NAME
this.PROBEDOCK_SECRET_KEY_NAME = env.PROBEDOCK_ENV + '-' + this.PROBEDOCK_SECRET_KEY_BASE_NAME
this.PROBEDOCK_JWT_SECRET_NAME = env.PROBEDOCK_ENV + '-' + this.PROBEDOCK_JWT_SECRET_BASE_NAME
this.PROBEDOCK_SMTP_USER_NAME = env.PROBEDOCK_ENV + '-' + this.PROBEDOCK_SMTP_USER_BASE_NAME
this.PROBEDOCK_SMTP_PASSWORD_NAME = env.PROBEDOCK_ENV + '-' + this.PROBEDOCK_SMTP_PASSWORD_BASE_NAME

/**
 * Define the var names in Docker Compose env vars
 */
this.DOCKER_POSTGRESQL_PASSWORD_VARNAME = 'POSTGRES_PASSWORD'
this.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME = 'PROBEDOCK_DATABASE_PASSWORD'
this.DOCKER_SECRET_KEY_NAME = 'PROBEDOCK_SECRET_KEY_BASE'
this.DOCKER_JWT_SECRET_NAME = 'PROBEDOCK_JWT_SECRET'
this.DOCKER_SMTP_USER_NAME = 'PROBEDOCK_MAIL_USERNAME'
this.DOCKER_SMTP_PASSWORD_NAME = 'PROBEDOCK_MAIL_PASSWORD'

return this