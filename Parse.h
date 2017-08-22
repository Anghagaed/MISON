#ifndef PARSE_HANG_COLIN_YUN
#define PARSE_HANG_COLIN_YUN
#include <string>
#include <iostream>
#include <vector>
#include <unordered_map>
#include <functional>
#include "Bitmaps.h"


typedef unordered_map<unsigned, string> stringmap;

using namespace std;

class parser {
private:
	stringmap queryFields;
	string line;
	bitmaps words;
	int hashCheck;
private:
	int parseWord(int level, string append, vector<int>& colonP, vector<pair<string, string> >& result);
public:
	parser(stringmap queryFields);
	int parseWord(string line, bitmaps words, vector<pair<string, string> >& result);

};
#endif