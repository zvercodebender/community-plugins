#!/bin/sh

<#if deployed.file??>
# do not remove - this actually triggers the upload
cd "${deployed.file}"
</#if>

if [ ! -d "${deployed.targetPath}" ]; then
  echo WARN: '${deployed.targetPath}' not found. Nothing to do.
  exit
fi
<#if deployed.targetPathShared>
echo Deleting from shared path '${deployed.targetPath}'
for ORIGINAL_FILE in "./*"; do
  FILE_TO_DELETE=${deployed.targetPath}/`basename $ORIGINAL_FILE`
  rm -rf "$FILE_TO_DELETE"
done
<#else/>
echo Deleting folder '${deployed.targetPath}'
rm -rf "${deployed.targetPath}"
</#if>
res=$?
if [ $res != 0 ] ; then
  exit $res
fi