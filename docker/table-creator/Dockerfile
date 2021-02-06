FROM alpine:3.12

ENV ENTRYKIT_VERSION 0.4.0

RUN apk --update add \
       openssl \
  && rm -rf /var/cache/apk/* \
  && wget https://github.com/progrium/entrykit/releases/download/v${ENTRYKIT_VERSION}/entrykit_${ENTRYKIT_VERSION}_Linux_x86_64.tgz \
  && tar -xvzf entrykit_${ENTRYKIT_VERSION}_Linux_x86_64.tgz \
  && rm entrykit_${ENTRYKIT_VERSION}_Linux_x86_64.tgz \
  && mv entrykit /bin/entrykit \
  && chmod +x /bin/entrykit \
  && entrykit --symlink

RUN apk add --no-cache \
        python3 \
        py3-pip \
    && pip3 install --upgrade pip \
    && pip3 install \
        awscli \
    && rm -rf /var/cache/apk/*

RUN mkdir /table-creator
WORKDIR /table-creator

COPY docker/table-creator/foosTable.json foosTable.json
COPY docker/table-creator/fooItems.json fooItems.json

ENTRYPOINT [ \
    "prehook", "aws dynamodb create-table --cli-input-json file://foosTable.json --endpoint-url http://dynamodb-local:8000", "--", \
    "prehook", "aws dynamodb batch-write-item --request-items file://fooItems.json --return-consumed-capacity TOTAL --endpoint http://dynamodb-local:8000", "--" \
]
