#ifndef _FileHandler_H
#define _FileHandler_H

#include <string>
#include <fstream>
#include <vector>
#include <iostream>
#include <cstdlib>

using namespace std;

class fileHandler{
private:
	string filePath;
public:
	vector<string> tuples;
	string text;
	//constructor
	fileHandler(string filePath);

	//returns the size of the string
	int getSize();

	//returns the requested tuple
	string getTuple(int i);

	//returns a the file split up into 32 size chunks in a vector of strings
	vector <string> split();
};
	

#endif