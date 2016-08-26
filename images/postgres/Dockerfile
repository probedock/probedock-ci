FROM postgres:9.5.2

COPY scripts/init.sh docker-entrypoint-initdb.d/init.sh

COPY scripts/backup.sh /scripts/backup.sh
COPY scripts/drop-and-create.sh /scripts/drop-and-create.sh
COPY scripts/load-dump.sh /scripts/load-dump.sh

