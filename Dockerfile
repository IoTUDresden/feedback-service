FROM niaquinto/gradle
MAINTAINER Peter Heisig <peter.heisig@tu-dresden.de>

WORKDIR /tmp

ENV SPRING_DATA_ELASTICSEARCH_CLUSTER-NODES elasticsearch:9300
ENV SERVICE_KNOWLEDGE_NEO4J_URL http://neo4j:7474

ENTRYPOINT ["gradle"]
CMD ["bootRun"]
