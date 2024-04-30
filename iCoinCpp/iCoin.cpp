#include <iostream>
#include "BlockChain.h"

int main(int argc, char* argv[])
{
    Block block1("转账十元");
    Block block2("转账十个十元");

    Chain chain;
    chain.setDifficulty(3);//设置难度
    chain.addBlock2Chain(block1);
    chain.addBlock2Chain(block2);

    std::cout << chain.toString() << std::endl;
    //尝试篡改这个区块链
    chain.getBlockAtIndex(1).setData("转账一百个十元");
    chain.getBlockAtIndex(1).setBlockHash(chain.getBlockAtIndex(1).mine(chain.getDifficulty()));
    std::cout << chain.toString() << std::endl;
    std::cout << chain.validateChain() << std::endl;

    std::cout << chain.toString() << std::endl;
    return 0;
}