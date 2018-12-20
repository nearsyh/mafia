#!/bin/bash

# Path to this plugin, Note this must be an abolsute path on Windows (see #15)
PROTOC_GEN_TS_PATH="./node_modules/.bin/protoc-gen-ts"

# Directory to write generated code to (.js and .d.ts files)
OUT_DIR="./src/protos"

PROTO_DIR="../protos/src/main/proto/"
PROTO_FILES="game.proto"

rm -rf ${OUT_DIR}/*

protoc \
    -I${PROTO_DIR} \
    --plugin="protoc-gen-ts=${PROTOC_GEN_TS_PATH}" \
    --js_out="import_style=commonjs,binary:${OUT_DIR}" \
    --ts_out="${OUT_DIR}" \
    $PROTO_FILES

git add generate.sh
git add src/protos
git commit -m "Update protobuf"
