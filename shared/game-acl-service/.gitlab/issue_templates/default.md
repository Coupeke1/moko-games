## Gedetailleerde omschrijving

*Invullen*

## Aanpassingen aan het domeinmodel

*Invullen*



## Taken
* [ ] *Taken aanmaken*


## Acceptatiecriteria

```
*Invullen*

  Background:
    Given Students
    | name | email      |  id|
    | Jan  | jan@kdg.be |  1 |
    | Piet | piet@kdg.be|  2 |

    Scenario: Student subscribes to a course
      Given student with id 1 edits subscription with id 1
      When student adds course with id 2
      Then subscription with id 1 has a courseSubscriptions with courseId 2
```

## Definition of ready checklist 
[ ] *Invullen van voorwaarden*
