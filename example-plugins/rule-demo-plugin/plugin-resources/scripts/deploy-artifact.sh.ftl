echo "Deploying file (Unix)"
ls ${deployed.file}
mkdir -p ${deployed.container.home + "/context"}
cp ${deployed.file} ${deployed.container.home + "/context"}
echo "Done"

