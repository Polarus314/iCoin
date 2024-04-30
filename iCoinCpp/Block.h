#ifndef BLOCK
#define BLOCK
#include <sstream>

//区块类
class Block {
private:
    DataType data;//区块中存储的数据
    HashType previousHash;//之前区块的哈希值
    HashType blockHash;//本区块的哈希值,自己的哈希值是由存储在区块里面的信息计算得到
    unsigned long long int nonce = 1;//随机数
    std::string timestamp;

    bool isFirstNCharsSame(const std::string& str, char ch, std::size_t n) {
        // 使用compare()函数比较前n个字符
        return str.compare(0, n, std::string(n, ch)) == 0;
    }

    char* now() {
        std::chrono::system_clock::time_point now = std::chrono::system_clock::now();

        // 将时间点转换为 C 的时间结构 tm
        std::time_t now_c = std::chrono::system_clock::to_time_t(now);
        struct tm* utc_tm = std::gmtime(&now_c);

        // 输出UTC时间信息
        return std::asctime(utc_tm);
    }

public:
    Block(DataType data) {
        this->data = data;
        this->previousHash = "";
        this->blockHash = computeHash(*this);
        this->timestamp = this->now();
        unsigned long long int nonce = 1;//随机数
    }
    Block(DataType data, HashType previousHash) {
        // 构造函数，其中 blockHash 是该区块数据和前一个区块哈希值拼接后使用哈希函数得出的结果
        this->data = data;
        this->previousHash = previousHash;
        this->blockHash = computeHash(*this);
        this->timestamp = this->now();
    }

    HashType computeHash(const std::string& src) {//传入待计算的字符串，用于计算哈希值
        return picosha2::hash256_hex_string(src);
    }

    HashType computeHash(const Block& block){//传入待计算的字符串，用于计算哈希值
        return block.computeHash();
    }

    HashType computeHash() const {
        return picosha2::hash256_hex_string(this->getData() + this->getPreviousHash() + std::to_string(this->nonce) + this->timestamp);
    }

    HashType mine(int difficulty) {//计算符合区块链难度要求的 hash
        HashType hash = this->computeHash();
        while (true) {
            if (isFirstNCharsSame(hash, '0', difficulty)) {//前 difficulty 位都为 0
                // std::cout << hash << std::endl;
                return hash;
            } else {
                ++(this->nonce);
                hash = this->computeHash();
            }
        }
    }

    std::string formatT(int tab) {
        std::string result = "";
        for (int i = 0 ; i < tab ; ++i) {
            result += "\t";
        }
        return result;
    }

    std::string toString() {//格式化输出区块的数据
        return this->toString(0);
    }

    std::string toString(int tab) {//格式化输出区块的数据
        return formatT(tab) + "Block: {\n" +
            formatT(tab) + "\tdata: \"" + this->data + "\",\n" +
            formatT(tab) + "\tpreviousHash: \"" + this->previousHash + "\",\n" +
            formatT(tab) + "\tblockHash: \"" + this->blockHash + "\",\n" +
            formatT(tab) + "\tnonce: \"" + std::to_string(this->nonce) + "\",\n" +
            formatT(tab) + "}";
    }

public:
    // 获取data的get和set函数
    DataType getData() const {
        return data;
    }

    void setData(const DataType& newData) {
        data = newData;
    }

    // 获取previousHash的get和set函数
    HashType getPreviousHash() const {
        return previousHash;
    }

    void setPreviousHash(const HashType& newPreviousHash) {
        previousHash = newPreviousHash;
    }

    // 获取blockHash的get和set函数
    HashType getBlockHash() const {
        return blockHash;
    }

    void setBlockHash(const HashType& newBlockHash) {
        blockHash = newBlockHash;
    }

};

#endif