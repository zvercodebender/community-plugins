#!/bin/sh

<#if deployed.targetFileName?has_content>
TARGET_FILE_NAME=${deployed.targetFileName}
<#else>
TARGET_FILE_NAME=${deployed.name}
</#if>

TARGET_FILE=${deployed.targetPath}/$TARGET_FILE_NAME

if [ ! -f "$TARGET_FILE" ]; then
  echo WARN: "$TARGET_FILE" not found. Nothing to do.
  exit
fi

echo Deleting file "$TARGET_FILE"
rm -r "$TARGET_FILE"
res=$?
if [ $res != 0 ] ; then
  exit $res
fi