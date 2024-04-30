#ifndef TRANSACTION
#define TRANSACTION
class Transaction {
private:
    std::string from;
    std::string to;
    double amount;

public:
    Transaction(std::string from, std::string to, double amount) {
        this->from = from;
        this->to = to;
        this->amount = amount;
    }  
};
#endif