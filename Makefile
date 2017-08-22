CC = g++ -std=c++11 -O0 -Wno-deprecated

tag = -i

ifdef linux
	tag = -n
endif


main.out:	Bitmaps.o Parse.o fileHandler.o main.o
	$(CC) -o main.out Bitmaps.o Parse.o fileHandler.o main.o
	
main.o:	main.cpp
	$(CC) -c main.cpp

Bitmaps.o: Bitmaps.h Bitmaps.cpp
	$(CC) -c Bitmaps.cpp

Parse.o: Parse.h Parse.cpp
	$(CC) -c Parse.cpp

fileHandler.o: fileHandler.h fileHandler.cpp
	$(CC) -c fileHandler.cpp

clean: 
	rm -f *.o
	rm -f *.out

