echo "Deploying file on Unix"
ls ${deployed.file.name}
mkdir -p ${deployed.container.home + "/context"}
cp ${deployed.file.name} ${deployed.container.home + "/context"}
echo "Done"

