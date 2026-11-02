ARG WODBY_BASE_IMAGE
FROM ${WODBY_BASE_IMAGE} AS builder

ARG COPY_FROM
WORKDIR /usr/src/app

# Download Gradle and the dependencies in a layer of their own, so a change to
# the source alone reuses them.
COPY --chown=wodby:wodby ${COPY_FROM}/gradlew ${COPY_FROM}/settings.gradle.kts ${COPY_FROM}/build.gradle.kts ./
COPY --chown=wodby:wodby ${COPY_FROM}/gradle gradle
RUN sh ./gradlew --no-daemon --console=plain dependencies > /dev/null

COPY --chown=wodby:wodby ${COPY_FROM} /usr/src/app
RUN sh ./gradlew --no-daemon --console=plain installDist

FROM ${WODBY_BASE_IMAGE}
COPY --from=builder --chown=wodby:wodby /usr/src/app/build/install/app /usr/src/app

CMD ["/usr/src/app/bin/app"]
