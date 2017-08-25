#include <string>
#include <iostream>
#include <vector>
#include <bitset>
#include <cassert>
#include <stack>
#include <algorithm>
#include <unordered_map>
#include <functional>
#include "fileHandler.h"
#include "Bitmaps.h"
#include "Parse.h"

using namespace std;

string defaultPath = "jsonTest5.txt";

int main(int argc, char *argv[]) {
	int layers = 2;
	int arraylayers = 4;
	vector<string> tester;
	string filePath;
	if (argc == 2) {
		filePath = argv[1];
	}
	else {
		filePath = defaultPath;
	}
	fileHandler fHandler(filePath);
	tester = fHandler.split();
	bitmaps test(layers, arraylayers, tester);
	test.bitsetCreate();

	cout << test;
	//test.printPhase4();
	cout << "Finished printing bitmaps" << endl; 

	vector<vector<int>> colonL;
	vector<int> colonP;

	vector<string> toHash;
	string temp;
	temp = "reviews"; toHash.push_back(temp);
	temp = "attributes.breakfast"; toHash.push_back(temp);
	temp = "attributes.lunch"; toHash.push_back(temp);

	stringmap queryFields;
	for (int i = 0; i < toHash.size(); ++i) {
		hash<string> ptr;
		pair<unsigned, string> toInsert(ptr(toHash[i]), toHash[i]);
		//cout << toInsert.first() << " is " << toInsert.second() << endl;
		queryFields.insert(toInsert);
	}
	for (auto& x : queryFields) { cout << x.first << " is " << x.second << endl; }
	vector<pair<string, string> > result;
	parser parserer(queryFields);
	if (parserer.parseWord(fHandler.text, test, result)) {
		cout << "The record fulfills the query" << endl;
	}
	else {
		cout << "The record does not fulfills the query" << endl;
	}


}