#include "fileHandler.h"

using namespace std;

//constructor
fileHandler::fileHandler(string filePath){
	this -> filePath = filePath;
	text = "";
}

//returns the size of the string
int fileHandler::getSize(){
	return text.size();
}

//returns the requested tuple
string fileHandler::getTuple(int i){
	return tuples[i];
}

//returns a the file split up into 32 size chunks in a vector of strings
vector <string> fileHandler::split(){
	ifstream inFile;
	//char* fileP = filePath.c_str();
	inFile.open(filePath.c_str());
	if (!inFile) {
		cerr << "Unable to open file " << filePath;
		exit(1);   // call system to stop
	}
	string nextLine;
	//string text = "";
	//read file and put it in a string
	while (!inFile.eof()){
		getline(inFile, nextLine);
		tuples.push_back(nextLine);
		text += nextLine;
	}
	vector<string> output;
	//split it up
	for(int i = 0; i < text.size(); i += 32){
		//std::cout << i << std::endl;
		if(i + 32 <= text.size()){
			//std::cout << text.substr(i, 32) << std::endl;
			output.push_back(text.substr(i, 32));
		}
		else{	//leftover case
			//std::cout << "we only go in here once" << std::endl;
			output.push_back(text.substr(i, text.size()));
		}
	}
	inFile.close();
	return output;
}