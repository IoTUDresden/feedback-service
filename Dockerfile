FROM niaquinto/gradle
MAINTAINER Peter Heisig <peter.heisig@tu-dresden.de>

WORKDIR /tmp

ENTRYPOINT ["gradle"]
CMD ["bootRun"]
