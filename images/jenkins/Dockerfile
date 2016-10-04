FROM jenkinsci/jenkins

USER root

RUN apt-get update && \
    apt-get install apt-transport-https && \
    apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D && \
    echo "deb https://apt.dockerproject.org/repo debian-jessie main" | tee /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install docker-engine -y && \
    curl -L https://github.com/docker/compose/releases/download/1.8.1/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose && \
    chmod +x //usr/local/bin/docker-compose && \
    rm -rf /var/lib/apt/lists/*

COPY plugins.txt /usr/share/jenkins/plugins.txt

RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt

RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

COPY config /usr/share/jenkins/ref

COPY jobs /usr/share/jenkins/ref/jobs/

COPY scripts/wait-for-it.sh .
COPY scripts/start.sh .

RUN mkdir /envs

VOLUME /envs

ENTRYPOINT ["/bin/tini", "--", "./start.sh"]
