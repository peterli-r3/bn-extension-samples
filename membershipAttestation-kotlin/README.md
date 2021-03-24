# Membership Attestation

//Step1: Create the network 
flow start CreateNetwork

//Step2: 2 non-member makes the request to join the network. 
flow start RequestMembership authorisedParty: NetworkOperator, networkId: 2fb5a8f0-57d4-4fd8-9253-bb4fbc9e79c2

//Step3: go back to the admin node, and query all the membership requests. 
flow start QueryAllMembers

//Step4: Admin active membership, two times, ONLY the membership activation 
Insurance: 
flow start ActiveMembers membershipId: 6f066bc5-37e6-485c-bc2b-8ace94e2c05d

CarePro:
flow start ActiveMembers membershipId: 7e9a3d4d-27a6-4342-a8d8-837b1e131f8c

//Step5: Admin create subgroup and add group members. 
flow start CreateNetworkSubGroup networkId: 2fb5a8f0-57d4-4fd8-9253-bb4fbc9e79c2, groupName: APAC_Insurance_Alliance, groupParticipants: [ca938975-a6c0-449b-a188-ec620e7f0ab9, 6f066bc5-37e6-485c-bc2b-8ace94e2c05d, 7e9a3d4d-27a6-4342-a8d8-837b1e131f8c]

//Step6: Admin assign business identity to a member. 
flow start AssignBNIdentity firmType: InsuranceFirm, membershipId: 6f066bc5-37e6-485c-bc2b-8ace94e2c05d, bnIdentity: APACIN76CZX

//Step7: Admin assign business identity related ROLE to the member.
flow start AssignPolicyIssuerRole membershipId: 6f066bc5-37e6-485c-bc2b-8ace94e2c05d, networkId: 2fb5a8f0-57d4-4fd8-9253-bb4fbc9e79c2

//Step8: Admin assign business identity to the second member 
flow start AssignBNIdentity firmType: CareProvider, membershipId: 7e9a3d4d-27a6-4342-a8d8-837b1e131f8c, bnIdentity: APACCP44OJS

//Query to check
run vaultQuery contractStateType: net.corda.core.contracts.ContractState

-------------------Network setup is done, and business flow begins--------------------------

/* Step9: The insurance Company will issue a policy to insuree. 
 * The flow initiator (the insurance company) has to be a member of the Business network, has to have a insuranceIdentity, and has to have issuer Role, and has to have issuance permission. 
 */
flow start IssuePolicy networkId: 2fb5a8f0-57d4-4fd8-9253-bb4fbc9e79c2, careProvider: CarePro, insuree: PeterLi
   
//Step10: Query the state in the CarePro node. 
run vaultQuery contractStateType: net.corda.samples.businessmembership.states.InsuranceState



//flow start AssignBNIdentity firmType: CareProvider, membershipId: 9cc6abf7-e20e-4404-be8c-4bcec1a668e0, bnIdentity: APACCP44OJS
