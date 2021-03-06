import org.apache.commons.lang.StringUtils;
import org.drugis.addis.outcomes.OutcomeCommand;
import org.drugis.addis.trialverse.model.SemanticOutcome;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by daan on 3/12/14.
 */
public class OutcomeCommandTest {
  @Test
  public void testGet() {
    OutcomeCommand outcomeCmdWithNoMotivation = new OutcomeCommand(1, "name", null, new SemanticOutcome("uri", "label"));
    assertEquals(StringUtils.EMPTY, outcomeCmdWithNoMotivation.getMotivation());

    String motivation = "motivation";
    OutcomeCommand filledCommand = new OutcomeCommand(1, "name", motivation, new SemanticOutcome("uri", "label"));
    assertEquals(motivation, filledCommand.getMotivation());
  }
}
