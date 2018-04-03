use bm2

 
Select	Distinct
		e.id as 'Unique ID (GUID)',
		e.Name as 'Name of home/provider',
		Address,
		Case when CustomfieldsValues.value ('(ArrayOfEventAdditionalFieldValueDto/EventAdditionalFieldValueDto/Value)[1]','varchar(100)')= '' then Ltrim(Substring(postcode,0,Charindex(postcode,' ',0))) 
				Else Ltrim(CustomfieldsValues.value ('(ArrayOfEventAdditionalFieldValueDto/EventAdditionalFieldValueDto/Value)[1]','varchar(100)')) end as [PostCode1],
		Case when CustomfieldsValues.value ('(ArrayOfEventAdditionalFieldValueDto/EventAdditionalFieldValueDto/Value)[2]','varchar(100)')= '' then Ltrim(Substring(postcode,0,Charindex(postcode,' ',0))) 
				Else Ltrim(CustomfieldsValues.value ('(ArrayOfEventAdditionalFieldValueDto/EventAdditionalFieldValueDto/Value)[2]','varchar(100)')) end as [PostCode2],
		Phone,
		Website,
		email as 'Public email',
		asv1.name as 'Category of home',
		case when asv2.Id is null then '' Else 'Yes' End as 'Short Stay',
		case when asv3.Id is null then '' Else 'Yes' End as 'Informal Stay',
		case when asv4.Id is null then '' Else 'Yes' End as 'Formal Stay',
		case when asv5.Id is null then '' Else 'Yes' End as 'Long Stay',
		case when asv10.Id is null then '' Else 'Yes' End as 'Dementia',
		case when asv6.Id is null then '' Else 'Yes' End as 'Mental Health',
		case when asv11.Id is null then '' Else 'Yes' End as 'Learning Disabilities',
		case when asv7.Id is null then '' Else 'Yes' End as 'Old Age',
		case when asv8.Id is null then '' Else 'Yes' End as 'PHysical Disabilities',
		case when asv13.Id is null then '' Else 'Yes' End as 'Sensory Impairment',
		case when asv12.Id is null then '' Else 'Yes' End as 'Past or Present Alcohol dependence',
		case when asv9.Id is null then '' Else 'Yes' End as 'Past or Present drug dependence',
		Case when CustomfieldsValues.value ('(ArrayOfEventAdditionalFieldValueDto/EventAdditionalFieldValueDto/Value)[9]','varchar(100)')= '' then ''
				Else Ltrim(CustomfieldsValues.value ('(ArrayOfEventAdditionalFieldValueDto/EventAdditionalFieldValueDto/Value)[9]','varchar(100)')) end as [Latitude],
		Case when CustomfieldsValues.value ('(ArrayOfEventAdditionalFieldValueDto/EventAdditionalFieldValueDto/Value)[10]','varchar(100)')= '' then ''
				Else Ltrim(CustomfieldsValues.value ('(ArrayOfEventAdditionalFieldValueDto/EventAdditionalFieldValueDto/Value)[10]','varchar(100)')) end as [Longtitude],
		asv.name as 'Town'		
from event e 
join eventtemplate et					on et.id=e.eventtemplateid
Left join EventAttributeValue eav		on eav.EventId=e.id and eav.ValueId in (Select id from AttributeSelectionValue where attributeid='2B5968BB-B99C-4AFD-B04E-A5DA0093D88C') --Town
Left join AttributeSelectionValue asv	on asv.id=eav.ValueId
Left join EventAttributeValue eav1		on eav1.EventId=e.id and eav1.ValueId in (Select id from AttributeSelectionValue where attributeid='6B2ACC0B-4A81-4D71-9F1E-A5DA008F9A9E') --Town
Left join AttributeSelectionValue asv1	on asv1.id=eav1.ValueId
Left join EventAttributeValue eav2		on eav2.EventId=e.id and eav2.ValueId ='9CEF42A0-26C0-4410-B193-A609010A1CD7' -- Short stay
Left join AttributeSelectionValue asv2	on asv2.id=eav2.ValueId

Left join EventAttributeValue eav3		on eav3.EventId=e.id and eav3.ValueId ='A5949129-1265-4D30-8FB2-A609010A1CD7'	--Formal
Left join AttributeSelectionValue asv3	on asv3.id=eav3.ValueId

Left join EventAttributeValue eav4		on eav4.EventId=e.id and eav4.ValueId ='84F215B5-D2EC-4745-9CA8-A609010A1CD7'	-- Informal
Left join AttributeSelectionValue asv4	on asv4.id=eav3.ValueId

Left join EventAttributeValue eav5		on eav5.EventId=e.id and eav5.ValueId ='F28AC7F8-750E-4BB9-B4FA-A609010A1CD7'	-- Long
Left join AttributeSelectionValue asv5	on asv5.id=eav5.ValueId

Left join EventAttributeValue eav6		on eav6.EventId=e.id and eav6.ValueId ='1955AEA7-A30B-414F-85E3-A5DA00909137'   -- Mental health condition
Left join AttributeSelectionValue asv6	on asv6.id=eav6.ValueId

Left join EventAttributeValue eav7		on eav7.EventId=e.id and eav7.ValueId ='2D707710-74A2-43DE-9200-A5DA00909137'	-- Old Age
Left join AttributeSelectionValue asv7	on asv7.id=eav7.ValueId

Left join EventAttributeValue eav8		on eav8.EventId=e.id and eav8.ValueId ='8655B416-93D1-4151-9A70-A5DA00909137'	-- Physical
Left join AttributeSelectionValue asv8	on asv8.id=eav8.ValueId

Left join EventAttributeValue eav9		on eav9.EventId=e.id and eav9.ValueId ='840A75EB-98D8-4CDB-9C74-A5DA00909137'	--Past or present drug dependence
Left join AttributeSelectionValue asv9	on asv9.id=eav9.ValueId

Left join EventAttributeValue eav10		on eav10.EventId=e.id and eav10.ValueId ='9D443E2C-7E0A-4ED5-A36E-A5DA00909137'	--[Dementia1]
Left join AttributeSelectionValue asv10	on asv10.id=eav10.ValueId

Left join EventAttributeValue eav11		on eav11.EventId=e.id and eav11.ValueId ='42234739-C32D-4C76-A422-A5DA00909137'	--[Learning disabilities1]
Left join AttributeSelectionValue asv11	on asv11.id=eav11.ValueId

Left join EventAttributeValue eav12		on eav12.EventId=e.id and eav12.ValueId ='DECA77C3-2C7E-4EA6-A773-A5DA00909137'	--Past or present alcohol dependence
Left join AttributeSelectionValue asv12	on asv12.id=eav12.ValueId

Left join EventAttributeValue eav13		on eav2.EventId=e.id and eav13.ValueId ='1C8AA2B6-8702-43E9-A8CD-A5DA00909137'	--[Sensory impairment1]
Left join AttributeSelectionValue asv13	on asv2.id=eav13.ValueId


where organisationid in (Select id from organisationbase where name like '%Hampshire%') --and ispublished=0
and et.id in ('E33A53BA-0E9C-4772-80F8-A5ED00F38F34')
order by e.name

