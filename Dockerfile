FROM niaquinto/gradle
MAINTAINER Peter Heisig <peter.heisig@tu-dresden.de>

ENTRYPOINT ["gradle"]
CMD ["bootRun"]
