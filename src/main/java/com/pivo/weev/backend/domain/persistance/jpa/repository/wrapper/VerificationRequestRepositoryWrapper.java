package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.VERIFICATION_REQUEST;

import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.persistance.jpa.model.auth.VerificationRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IVerificationRequestRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class VerificationRequestRepositoryWrapper extends GenericRepositoryWrapper<Long, VerificationRequestJpa, IVerificationRequestRepository> {

    protected VerificationRequestRepositoryWrapper(IVerificationRequestRepository repository) {
        super(repository, VERIFICATION_REQUEST);
    }

    public Optional<VerificationRequestJpa> findByContacts(Contacts contacts) {
        if (contacts.hasEmail()) {
            return findByEmail(contacts.getEmail());
        }
        if (contacts.hasPhoneNumber()) {
            return findByPhoneNumber(contacts.getPhoneNumber());
        }
        return Optional.empty();
    }

    private Optional<VerificationRequestJpa> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    private Optional<VerificationRequestJpa> findByPhoneNumber(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber);
    }

}
