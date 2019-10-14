package ofedorova.application.account;

import ofedorova.domain.account.Account;
import ofedorova.domain.account.AccountRepository;

import java.util.UUID;

public class AccountService {

  private final AccountRequestValidator requestValidator;
  private final AccountRepository accountRepository;

  public AccountService(AccountRequestValidator requestValidator, AccountRepository accountRepository) {
    this.requestValidator = requestValidator;
    this.accountRepository = accountRepository;
  }

  public void create(CreateRequest createRequest) {
    requestValidator.validate(createRequest);
    Account account = new Account(createRequest.getUserId(), createRequest.getTitle());
    accountRepository.create(account);
  }

  public void deposit(UpdateRequest updateRequest) {
    requestValidator.validate(updateRequest);
    Account account = accountRepository.findById(updateRequest.getAccountId());
    account.deposit(updateRequest.getAmount());
  }

  public void withdraw(UpdateRequest updateRequest) {
    requestValidator.validate(updateRequest);
    Account account = accountRepository.findById(updateRequest.getAccountId());
    account.withdraw(updateRequest.getAmount());
  }

  public Account findById(UUID id) {
    return accountRepository.findById(id);
  }

  public Account findByUserIdAndTitle(UUID userId, String title) {
    return accountRepository.findByUserIdAndTitle(userId, title);
  }


}
