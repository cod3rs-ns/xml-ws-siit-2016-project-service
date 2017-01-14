package rs.ac.uns.sw.xml.states;


import org.springframework.http.ResponseEntity;
import rs.ac.uns.sw.xml.domain.Amendments;
import rs.ac.uns.sw.xml.domain.Law;
import rs.ac.uns.sw.xml.domain.Parliament;
import rs.ac.uns.sw.xml.util.StateConstants;

import static rs.ac.uns.sw.xml.util.HeaderUtil.forbiddenActionFromState;

public class CanceledState implements State {

    @Override
    public ResponseEntity<?> suggestLaw(Law law, Parliament parliament) {
        return forbiddenActionFromState(StateConstants.ParliamentStates.CANCELED_STATE);
    }

    @Override
    public ResponseEntity<?> suggestAmendments(Amendments amendments, Parliament parliament) {
        return forbiddenActionFromState(StateConstants.ParliamentStates.CANCELED_STATE);
    }

    @Override
    public ResponseEntity<?> updateLawStatus(String id, String status, Parliament parliament) {
        return forbiddenActionFromState(StateConstants.ParliamentStates.CANCELED_STATE);
    }

    @Override
    public ResponseEntity<?> updateAmendmentStatus(String id, String status, Parliament parliament) {
        return forbiddenActionFromState(StateConstants.ParliamentStates.CANCELED_STATE);
    }

    @Override
    public ResponseEntity<?> voting(int votesFor, int votesAgainst, int votesNeutral) {
        return forbiddenActionFromState(StateConstants.ParliamentStates.CANCELED_STATE);
    }
}
