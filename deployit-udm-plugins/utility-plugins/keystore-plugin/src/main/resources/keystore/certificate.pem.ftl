<#assign base64 = new("javax.xml.bind.DatatypeConverter")>
${base64.parseBase64Binary(deployed.certificate)}