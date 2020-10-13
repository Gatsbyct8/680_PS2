/**
 * 
 */


import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2011
 */
public class TestCases extends CyclicIterator<Map<String, Angled>> {

  Map<String, Angled> stop() {
    return this.stop;
  }

  private final Map<String, Angled> stop;

  @SuppressWarnings("unchecked")
  TestCases() {
    this.stop = new HashMap<String, Angled>();
    final Map<String, Angled> peace = new HashMap<String, Angled>();
    final Map<String, Angled> fist = new HashMap<String, Angled>();
    final Map<String, Angled> shaka = new HashMap<String, Angled>();
    final Map<String, Angled> spread = new HashMap<String, Angled>();
    final Map<String, Angled> claw = new HashMap<String, Angled>();

    super.add(stop, peace, fist, shaka, spread, claw);

    // the upper arm, forearm, and hand angles do not change through any of the
    // test cases
    stop.put(PA2.RIGHT_BODY_NAME, new BaseAngled(0, 0, 0));
    peace.put(PA2.RIGHT_BODY_NAME, new BaseAngled(0, 0, 0));
    fist.put(PA2.RIGHT_BODY_NAME, new BaseAngled(0, 0, 0));
    shaka.put(PA2.RIGHT_BODY_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.RIGHT_BODY_NAME, new BaseAngled(0, 0, 0));
    claw.put(PA2.RIGHT_BODY_NAME, new BaseAngled(0, 0, 0));

    stop.put(PA2.MIDDLE_BODY_NAME, new BaseAngled(0, 90, 0));
    peace.put(PA2.MIDDLE_BODY_NAME, new BaseAngled(0, 90, 0));
    fist.put(PA2.MIDDLE_BODY_NAME, new BaseAngled(0, 90, 0));
    shaka.put(PA2.MIDDLE_BODY_NAME, new BaseAngled(0, 90, 0));
    spread.put(PA2.MIDDLE_BODY_NAME, new BaseAngled(0, 90, 0));
    claw.put(PA2.MIDDLE_BODY_NAME, new BaseAngled(0, 90, 0));

    stop.put(PA2.LEFT_BODY_NAME, new BaseAngled(0, 0, 0));
    peace.put(PA2.LEFT_BODY_NAME, new BaseAngled(0, 0, 0));
    fist.put(PA2.LEFT_BODY_NAME, new BaseAngled(0, 0, 0));
    shaka.put(PA2.LEFT_BODY_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.LEFT_BODY_NAME, new BaseAngled(0, 0, 0));
    claw.put(PA2.LEFT_BODY_NAME, new BaseAngled(0, 0, 0));

    // the stop test case
    stop.put(PA2.PINKY_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.PINKY_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.PINKY_LIMB_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.RING_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.RING_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.RING_LIMB_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.MIDDLE_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.MIDDLE_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.MIDDLE_LIMB_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.INDEX_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.INDEX_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.INDEX_LIMB_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.CLAW_DISTAL_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.CLAW_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.CLAW_NAME, new BaseAngled(90, 0, 0));

    // the peace sign test case
    peace.put(PA2.PINKY_DISTAL_NAME, new BaseAngled(50, 0, 0));
    peace.put(PA2.PINKY_MIDDLE_NAME, new BaseAngled(90, 0, 0));
    peace.put(PA2.PINKY_LIMB_NAME, new BaseAngled(60, 0, 0));
    peace.put(PA2.RING_DISTAL_NAME, new BaseAngled(50, 0, 0));
    peace.put(PA2.RING_MIDDLE_NAME, new BaseAngled(90, 0, 0));
    peace.put(PA2.RING_LIMB_NAME, new BaseAngled(60, 0, 0));
    peace.put(PA2.MIDDLE_DISTAL_NAME, new BaseAngled(0, 0, 0));
    peace.put(PA2.MIDDLE_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    peace.put(PA2.MIDDLE_LIMB_NAME, new BaseAngled(0, 0, 0));
    peace.put(PA2.INDEX_DISTAL_NAME, new BaseAngled(0, 0, 0));
    peace.put(PA2.INDEX_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    peace.put(PA2.INDEX_LIMB_NAME, new BaseAngled(0, 0, 0));
    peace.put(PA2.CLAW_DISTAL_NAME, new BaseAngled(10, 0, 0));
    peace.put(PA2.CLAW_MIDDLE_NAME, new BaseAngled(0, 2, 0));
    peace.put(PA2.CLAW_NAME, new BaseAngled(92, 0, 0));

    // the fist test case
    fist.put(PA2.PINKY_DISTAL_NAME, new BaseAngled(50, 0, 0));
    fist.put(PA2.PINKY_MIDDLE_NAME, new BaseAngled(90, 0, 0));
    fist.put(PA2.PINKY_LIMB_NAME, new BaseAngled(60, 0, 0));
    fist.put(PA2.RING_DISTAL_NAME, new BaseAngled(50, 0, 0));
    fist.put(PA2.RING_MIDDLE_NAME, new BaseAngled(90, 0, 0));
    fist.put(PA2.RING_LIMB_NAME, new BaseAngled(60, 0, 0));
    fist.put(PA2.MIDDLE_DISTAL_NAME, new BaseAngled(50, 0, 0));
    fist.put(PA2.MIDDLE_MIDDLE_NAME, new BaseAngled(90, 0, 0));
    fist.put(PA2.MIDDLE_LIMB_NAME, new BaseAngled(60, 0, 0));
    fist.put(PA2.INDEX_DISTAL_NAME, new BaseAngled(50, 0, 0));
    fist.put(PA2.INDEX_MIDDLE_NAME, new BaseAngled(90, 0, 0));
    fist.put(PA2.INDEX_LIMB_NAME, new BaseAngled(60, 0, 0));
    fist.put(PA2.CLAW_DISTAL_NAME, new BaseAngled(50, 0, 0));
    fist.put(PA2.CLAW_MIDDLE_NAME, new BaseAngled(0, 4, 0));
    fist.put(PA2.CLAW_NAME, new BaseAngled(94, 0, 0));

    // the shaka test case
    shaka.put(PA2.PINKY_DISTAL_NAME, new BaseAngled(0, 0, 0));
    shaka.put(PA2.PINKY_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    shaka.put(PA2.PINKY_LIMB_NAME, new BaseAngled(0, -15, 0));
    shaka.put(PA2.RING_DISTAL_NAME, new BaseAngled(50, 0, 0));
    shaka.put(PA2.RING_MIDDLE_NAME, new BaseAngled(80, 0, 0));
    shaka.put(PA2.RING_LIMB_NAME, new BaseAngled(45, 0, 0));
    shaka.put(PA2.MIDDLE_DISTAL_NAME, new BaseAngled(50, 0, 0));
    shaka.put(PA2.MIDDLE_MIDDLE_NAME, new BaseAngled(80, 0, 0));
    shaka.put(PA2.MIDDLE_LIMB_NAME, new BaseAngled(45, 0, 0));
    shaka.put(PA2.INDEX_DISTAL_NAME, new BaseAngled(50, 0, 0));
    shaka.put(PA2.INDEX_MIDDLE_NAME, new BaseAngled(80, 0, 0));
    shaka.put(PA2.INDEX_LIMB_NAME, new BaseAngled(45, 0, 0));
    shaka.put(PA2.CLAW_DISTAL_NAME, new BaseAngled(-10, 0, 0));
    shaka.put(PA2.CLAW_MIDDLE_NAME, new BaseAngled(0, 6, 0));
    shaka.put(PA2.CLAW_NAME, new BaseAngled(96, 0, 0));

    // the spread test case
    spread.put(PA2.PINKY_DISTAL_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.PINKY_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.PINKY_LIMB_NAME, new BaseAngled(0, -10, 0));
    spread.put(PA2.RING_DISTAL_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.RING_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.RING_LIMB_NAME, new BaseAngled(0, -7, 0));
    spread.put(PA2.MIDDLE_DISTAL_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.MIDDLE_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.MIDDLE_LIMB_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.INDEX_DISTAL_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.INDEX_MIDDLE_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.INDEX_LIMB_NAME, new BaseAngled(0, 10, 0));
    spread.put(PA2.CLAW_DISTAL_NAME, new BaseAngled(0, 0, 0));
    spread.put(PA2.CLAW_MIDDLE_NAME, new BaseAngled(0, 8, 0));
    spread.put(PA2.CLAW_NAME, new BaseAngled(98, 0, 0));

    // the claw test case
    claw.put(PA2.PINKY_DISTAL_NAME, new BaseAngled(60, 0, 0));
    claw.put(PA2.PINKY_MIDDLE_NAME, new BaseAngled(80, 0, 0));
    claw.put(PA2.PINKY_LIMB_NAME, new BaseAngled(0, 0, 0));
    claw.put(PA2.RING_DISTAL_NAME, new BaseAngled(60, 0, 0));
    claw.put(PA2.RING_MIDDLE_NAME, new BaseAngled(80, 0, 0));
    claw.put(PA2.RING_LIMB_NAME, new BaseAngled(0, 0, 0));
    claw.put(PA2.MIDDLE_DISTAL_NAME, new BaseAngled(60, 0, 0));
    claw.put(PA2.MIDDLE_MIDDLE_NAME, new BaseAngled(80, 0, 0));
    claw.put(PA2.MIDDLE_LIMB_NAME, new BaseAngled(0, 0, 0));
    claw.put(PA2.INDEX_DISTAL_NAME, new BaseAngled(60, 0, 0));
    claw.put(PA2.INDEX_MIDDLE_NAME, new BaseAngled(80, 0, 0));
    claw.put(PA2.INDEX_LIMB_NAME, new BaseAngled(0, 0, 0));
    claw.put(PA2.CLAW_DISTAL_NAME, new BaseAngled(70, 0, 0));
    claw.put(PA2.CLAW_MIDDLE_NAME, new BaseAngled(0, 10, 0));
    claw.put(PA2.CLAW_NAME, new BaseAngled(100, 0, 0));
  }
}
