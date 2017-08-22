#include "Parse.h"

parser::parser(stringmap queryFields) {
	this->queryFields = queryFields;
}

int parser::parseWord(string line, bitmaps words, vector<pair<string, string> >& result) {
	this->hashCheck = 0;
	this->line = line;
	this->words = words;
	vector<int> colonP;
	words.generateColonPositions(0, line.size(), 0, colonP);
	string temp = "";
	parseWord(0, temp, colonP, result);
	cout << "HashCheck is " << hashCheck << endl;
	return 1;

}

// Return -1 if can't find it

int findQuotePos(string line, int pos, int& result) {
	int i = pos;
	while (i >= 0) {
		if (line[i] == '\"') {
			result = i + 1;
			return i + 1;
		}
		--i;
	}
	return -1;
}

// Return -1 if can't find it

int findDefaultPos(string line, int pos, int& result) {
	int i = pos;
	while (i < line.size()) {
		const char test = line[i];
		if (test == '}' || test == ',') {
			result = i;
			return i;
		}
		++i;
	}
	return -1;
}

int parser::parseWord(int level, string append, vector<int>& colonP, vector<pair<string, string> >& result) {
	hash<string> ptr;
	for (int i = 0; i < colonP.size(); ++i) {
		int pos;
		if (findQuotePos(this->line, colonP[i] - 2, pos) == -1) {
			cout << "Something went wrong" << endl;
		};
		string field = append + line.substr(pos, colonP[i] - 1 - pos);
		cout << "Found Field " << field << endl;
		/*
		if (pos == (colonP[i] - 2)) {
			field = line.substr(pos, 1);
		}
		else {
			field = line.substr(pos, colonP[i] - 1 - pos);
		}
		*/

		stringmap::const_iterator got = queryFields.find(ptr(field));
		if (got != queryFields.end()) {
			if (line[colonP[i] + 1] == '{') {
				vector<int> newColonP;
				words.generateColonPositions(colonP[i], colonP[i + 1], level + 1, newColonP);
				string betterAppend;
				if (level == 0) {
					betterAppend = field + '.';
				}
				else {
					betterAppend = append + '.' + field;
				}
				cout << "Going to next level and beyond" << endl;
				parseWord(level + 1, betterAppend, newColonP, result);
			}
			else if (line[colonP[i] + 1] == '[') {
				cout << "ARRAY FIELDS NOT SUPPORTED" << endl;
			}
			else {
				int endPos;
				findDefaultPos(this->line, colonP[i] + 1, endPos);
				const char fwdChar = line[colonP[i] + 1];
				int startPos = colonP[i] + 1;
				if (fwdChar == '\"') {
					startPos += 1;
					endPos -= 1;
				}
				string output = line.substr(startPos, endPos - startPos);
				pair<string, string> toPush(field, output);
				cout << "Found Output " << output << endl;
				result.push_back(toPush);
				++hashCheck;
			}
		}
		//if (hashCheck == queryFields.size())
			//break;
	}
}