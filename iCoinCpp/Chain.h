#ifndef CHAIN
#define CHAIN

//链类，并且提供生成祖先区块的函数
class Chain {
private:
    std::vector<Block> blockChain;
    int difficulty = 3;//难度

public:
    Chain() {//构造函数，调用 bigBang 函数并将祖先区块放进链中
        this->blockChain.push_back(bigBang());
    }

    // 添加区块到区块链上 
    void addBlock2Chain(Block newblock) {
        newblock.setPreviousHash(this->blockChain.back().getBlockHash());//新区块的 previous 哈希应该等于区块链最后一个区块的哈希
        newblock.setBlockHash(newblock.mine(this->difficulty));
        this->blockChain.push_back(newblock);
    }

    std::string formatT(int tab) {
        std::string result = "";
        for (int i = 0 ; i < tab ; ++i) {
            result += "\t";
        }
        return result;
    }
    
    //生成祖先区块并返回
    Block bigBang() {
        return Block("我是祖先", "");
    }
    
    // 获取整个区块链
    const std::vector<Block>& getBlockChain() const {
        return blockChain;
    }

    const int getChainLenght() const {
        return this->blockChain.size();
    }

    void setDifficulty(int newDifficulty) {
        this->difficulty = newDifficulty;
    }

    const int getDifficulty() const {
        return this->difficulty;
    }

    // 获取特定位置的区块
    Block& getBlockAtIndex(int index) {
        if (index >= 0 && index < this->getChainLenght()) {
            return blockChain[index];
        } else {
            // 处理越界情况，这里简单返回第一个区块
            std::cerr << "索引越界，返回第一个区块" << std::endl;
            return blockChain.front();
        }
    }

    //验证当前区块链是否合法：
    //1.当前的数据有没有被篡改
    //2.验证区块的previousHash是否等于前一个区块的 hash
    bool validateChain() {
        if (this->getChainLenght() == 1) {//验证祖先数据有没有被篡改 
            if (this->getBlockAtIndex(0).getBlockHash() != this->getBlockAtIndex(0).computeHash()) {
                return false;
            }
            return true;
        }

        //验证当前数据有没有被篡改 
        bool isAncestor = true;//祖先区块标记
        HashType previousHash = "";
        for (Block block : this->getBlockChain()) {
            if (isAncestor == true) {//仅在第一个祖先区块运行
                if (block.getBlockHash() != block.computeHash(block)) {
                    std::cout << block.getBlockHash() << std::endl;
                    std::cout << block.computeHash(block) << std::endl;

                    std::cout << "祖先区块验证错误\n";
                    return false;
                }
                previousHash = block.getBlockHash();
                isAncestor = false;
                continue;
            }
            if (block.getBlockHash() != block.computeHash(block)) {
                std::cout << "当前区块的哈希值不等于用区块算出来的哈希值，那么肯定是被篡改了，那么这个链不合法\n";
                return false;//当前区块的哈希值不等于用区块算出来的哈希值，那么肯定是被篡改了，那么这个链不合法
            }
            if (block.getPreviousHash() != previousHash) {
                std::cout << "当前区块内存储的前一个区块的哈希值，不等于前一个区块的哈希值，那么这个链已经断裂\n";
                return false;//当前区块内存储的前一个区块的哈希值，不等于前一个区块的哈希值，那么这个链已经断裂
            }
            previousHash = block.getBlockHash();
        }
        return true;
    }

    std::string toString() {//格式化输出区块的数据
        std::string result = "Chain: {\n"
            "\tblockChain: [\n";

        for (Block element : this->getBlockChain()) {
            result = result + element.toString(2) + ",\n";
        }

        return result + "\t]\n}";
    }
};

#endif