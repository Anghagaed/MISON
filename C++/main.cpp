#include <string>
#include <iostream>
#include <vector>
#include <bitset>
#include <cassert>
#include <stack>
#include <algorithm>
#include <unordered_map>
#include <functional>
#include <cstdlib>
#include "fileHandler.h"
#include "Bitmaps.h"
#include "Parse.h"

using namespace std;

string defaultPath = "Test/jsonTest1.txt";
int defaultLayers = 2;
int defaultArrayLayers = 4;

const FieldInfo CONSTFIELDINFO(0);

int main(int argc, char *argv[]) {
	int arraylayers = 4;
	vector<string> tester;
	string filePath;
	if (argc >= 2) {
		filePath = argv[1];
	}
	else {
		filePath = defaultPath;
	}
	if (argc >= 3) {
		defaultLayers = atoi(argv[2]);
	}
	else {
		filePath = defaultPath;
	}
	if (argc >= 4) {
		defaultArrayLayers = atoi(argv[3]);
	}
	else {
		filePath = defaultPath;
	}
	/*
	vector<FieldInfo> toTest;
	hashmap test3;
	string xxxx = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
	for (unsigned i = 0; i < 5; ++i) {
		pair<unsigned, FieldInfo> toInsert(i, CONSTFIELDINFO);
		test3.insert(toInsert);
	}
	
	for (unsigned i = 5; i < 10; ++i) {
		FieldInfo grr(i);
		grr.subField.push_back(xxxx.substr(i, 5));
		pair<unsigned, FieldInfo> toInsert(i, grr);
		test3.insert(toInsert);
	}
	
	for (unsigned i = 0; i < 10; ++i) {
		cout << "i is " << i << endl;
		hashmap::const_iterator got = test3.find(i);
		if (got != test3.end()) {
			cout << "got-> first is " << got->first << " and got->second is: " << endl;
			cout << "int: " << got->second.type << endl;
			cout << "subfield: ";
			for (int j = 0; j < got->second.subField.size(); ++j) {
				cout << got->second.subField[j];
			}
			cout << endl;
		}
		else {
			cout << "test3 did not find i" << endl;
		}
	}


	return 1;
	*/
	
	fileHandler fHandler(filePath);
	tester = fHandler.split();
	bitmaps test(defaultLayers, defaultArrayLayers, tester);
	test.bitsetCreate();

	cout << test;
	//test.printPhase4();
	cout << "Finished printing bitmaps" << endl; 

	vector<vector<int>> colonL;
	vector<int> colonP;

	vector<string> toHash;
	string temp;
	temp = "reviews"; toHash.push_back(temp);
	temp = "id"; toHash.push_back(temp);
	temp = "city"; toHash.push_back(temp);
	//temp = "attributes.lunch"; toHash.push_back(temp);

	hashmap queryFields;
	for (int i = 0; i < toHash.size(); ++i) {
		hash<string> ptr;
		pair<unsigned, FieldInfo > toInsert(ptr(toHash[i]), CONSTFIELDINFO);
		//cout << toInsert.first() << " is " << toInsert.second() << endl;
		queryFields.insert(toInsert);
	}
	for (auto& x : queryFields) { cout << x.first << " is " << x.second << endl; }
	vector<pair<string, string> > result;
	parser parserer(queryFields);
	if (parserer.parseWord(fHandler.text, test, result) == 1) {
		cout << "The record fulfills the query" << endl;
		for (int i = 0; i < result.size(); ++i) {
			cout << result[i].first << " is: " << result[i].second << endl;
		}
	}
	else {
		cout << "The record does not fulfills the query" << endl;
	}
	
	return 1;

}