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

int main() {
	int layers = 2;
	vector<string> tester;
	//string word = "{\"id\":\"id:\\\"a\\\"\",\"reviews\":50,\"a";
	//tester.push_back(word);
	//word = "ttributes\":{\"breakfast\":false, \"";
	//tester.push_back(word);

	fileHandler fHandler("jsonTest1.txt");
	tester = fHandler.split();
	bitmaps test(layers, tester);
	test.bitsetCreate();
	//test.printPhase4();

	vector<vector<int>> colonL;
	vector<int> colonP;

	vector<string> toHash;
	string temp;
	temp = "reviews"; toHash.push_back(temp);
	temp = "city"; toHash.push_back(temp);
	temp = "attributes.breakfast"; toHash.push_back(temp);
	temp = "attributes"; toHash.push_back(temp);
	//temp = "categories[]"; toHash.push_back(temp);
	//temp = "state"; toHash.push_back(temp);

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
	parserer.parseWord(fHandler.text, test, result);


}