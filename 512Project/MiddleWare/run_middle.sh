../Server/run_rmi.sh > /dev/null

# echo "Edit file run_middleware.sh to include instructions for launching the middleware"
echo '  $1 - server name of Cars'
echo '  $2 - server name of Rooms'
echo '  $3 - server name of Flights'

#adjust port number if necessary
rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false 1045

java -cp .:../Server/RMIInterface.jar -Djava.rmi.server.codebase=file:../Server/RMIInterface.jar MiddleWare.RMI.RMIMiddleware $1 $2 $3