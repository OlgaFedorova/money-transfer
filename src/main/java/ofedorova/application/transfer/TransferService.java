package ofedorova.application.transfer;

import ofedorova.domain.account.Account;
import ofedorova.domain.account.AccountRepository;

public class TransferService {

  private final AccountRepository accountRepository;
  private final TransferRequestValidator requestValidator;

  public TransferService(AccountRepository accountRepository, TransferRequestValidator requestValidator) {
    this.accountRepository = accountRepository;
    this.requestValidator = requestValidator;
  }

  public void transfer(TransferRequest request) {
    requestValidator.validate(request);
    Account from = accountRepository.findById(request.getAccountIdFrom());
    Account to = accountRepository.findById(request.getAccountIdTo());
    from.transfer(to, request.getAmount());
  }
}
