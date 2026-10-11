# Cinema Tickets Code Test

### Requirements

| Scenario                           | Expected result                  |
| ---------------------------------- | ---------------------------------|
| Account ID is zero or negative     | Reject booking                   |
| More than 25 tickets               | Reject booking                   |
| No ADULT ticket                    | Reject booking                   |
| More INFANT than ADULT             | Reject booking                   |
| Empty booking or zero tickets      | Reject booking                   |
| Negative ticket quantities         | Reject booking                   |
| Valid booking within all limits    | Process payment and reserve seats|


### Assumptions

One infant per adult lap
Requests with negative ticket count should be rejected
