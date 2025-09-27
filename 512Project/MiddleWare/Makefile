all: compile-MiddleWare-rmi

../Server/RMIInterface.jar: ../Server/Server/Interface/IResourceManager.java
	make -C ../Server/ RMIInterface.jar

compile-MiddleWare-rmi: MiddleWare/RMI/*.java MiddleWare/Common/*.java MiddleWare/Customers/*.java ../Server/RMIInterface.jar
	javac -cp ../Server/RMIInterface.jar MiddleWare/RMI/*.java MiddleWare/Common/*.java MiddleWare/Customers/*.java